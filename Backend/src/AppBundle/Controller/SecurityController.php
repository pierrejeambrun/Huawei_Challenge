<?php

namespace AppBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Method;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Route;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Security\Core\Authentication\AuthenticationManagerInterface;
use Symfony\Component\Security\Core\Authentication\AuthenticationProviderManager;
use Symfony\Component\Security\Core\Authentication\Token\Storage\TokenStorageInterface;
use Symfony\Component\Security\Core\Authentication\Token\UsernamePasswordToken;
use Symfony\Component\Security\Core\Encoder\UserPasswordEncoder;
use Symfony\Component\Security\Http\Authentication\AuthenticationUtils;

class SecurityController extends Controller
{
    /**
     * @var TokenStorageInterface
     */
    private $tokenStorage;

    /**
     * @var AuthenticationManagerInterface
     */
    private $authenticationManager;

    /**
     * @var string Uniquely identifies the secured area
     */
    private $providerKey;


    /**
     * @Route("/login", name="login")
     */
    public function loginAction(Request $request, AuthenticationUtils $authUtils)
    {
        // get the login error if there is one
        $error = $authUtils->getLastAuthenticationError();

        $username = $request['username'];
        $password = $request['password'];

        $encoder = new UserPasswordEncoder();
        $this->getDoctrine()->getRepository("AppBundle:User")->findBy({
            "username" => $username,
            "password" => $encoder->encodePassword($user, $plainPassword);
        })

        $unauthenticatedToken = new UsernamePasswordToken(
            $username,
            $password,
            $this->providerKey
        );

        $authenticatedToken = $this
            ->authenticationManager
            ->authenticate($unauthenticatedToken);

        $this->tokenStorage->setToken($authenticatedToken);

        // instances of Symfony\Component\Security\Core\Authentication\Provider\AuthenticationProviderInterface


        $authenticationManager = new AuthenticationProviderManager($providers);

        try {
        $authenticatedToken = $authenticationManager
            ->authenticate($unauthenticatedToken);
        } catch (AuthenticationException $failed) {
        // authentication failed
        }

        // last username entered by the user
        $lastUsername = $authUtils->getLastUsername();

        return new JsonResponse(array('name' => $lastUsername, 'error' => $error));
    }

}